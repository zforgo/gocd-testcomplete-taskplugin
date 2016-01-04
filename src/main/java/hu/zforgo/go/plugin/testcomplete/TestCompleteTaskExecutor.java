package hu.zforgo.go.plugin.testcomplete;

import com.thoughtworks.go.plugin.api.response.execution.ExecutionResult;
import com.thoughtworks.go.plugin.api.task.Console;
import com.thoughtworks.go.plugin.api.task.EnvironmentVariables;
import com.thoughtworks.go.plugin.api.task.TaskConfig;
import com.thoughtworks.go.plugin.api.task.TaskExecutionContext;
import com.thoughtworks.go.plugin.api.task.TaskExecutor;
import de.odysseus.el.ExpressionFactoryImpl;
import de.odysseus.el.util.SimpleContext;
import org.apache.commons.lang3.StringUtils;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TestCompleteTaskExecutor implements TaskExecutor {
	private final ExpressionFactory factory = new ExpressionFactoryImpl();

	@Override
	public ExecutionResult execute(TaskConfig taskConfig, TaskExecutionContext taskExecutionContext) {
		final Console console = taskExecutionContext.console();

		ProcessBuilder pb = buildProcess(taskConfig, taskExecutionContext);

		console.printLine("---------------------------------------------------------------------------------------");
		console.printLine("|                     Starting Smartbear Build Task                                   |");
		console.printLine("---------------------------------------------------------------------------------------");
		console.printLine("\nLaunching command: \n\t" + StringUtils.join(pb.command(), " "));
		console.printLine("\nAt: \n\t" + pb.directory().getAbsolutePath());

		Process p = null;
		try {
			p = pb.start();
			int code = p.waitFor();

			if (code != 0) {
				console.readErrorOf(p.getErrorStream());
				return ExecutionResult.failure("Test failure with exit code: " + code);
			} else {
				console.readOutputOf(p.getInputStream());
			}
		} catch (Exception e) {
			if (p != null) {
				console.readErrorOf(p.getErrorStream());
			}
			console.printLine("Fail: Exception while running tests: " + e.getMessage());
			return ExecutionResult.failure("Test failure");
		} finally {
			if (p != null) {
				p.destroy();
			}
		}

		return ExecutionResult.success("Test Success");
	}

	private ProcessBuilder buildProcess(TaskConfig taskConfig, TaskExecutionContext taskExecutionContext) {
		final Console console = taskExecutionContext.console();
		final ELContext elContext = createEvaluationContext(taskExecutionContext.environment());

		List<String> commands = new ArrayList<>();
		File f = new File(taskConfig.getValue(Config.PATH.paramName()), taskConfig.getValue(Config.EXECUTABLE.paramName()) + ".exe");
		commands.add(f.getAbsolutePath());
		commands.add(taskConfig.getValue(Config.SUITE.paramName()));
		commands.add("/ns");
		commands.add("/SilentMode");
		commands.add("/run");
		commands.add("/exit");

		String repoPath = taskConfig.getValue(Config.REPORTPATH.paramName());
		if (StringUtils.isNotBlank(repoPath)) {
			ValueExpression expr = factory.createValueExpression(elContext, repoPath, String.class);
			repoPath = (String) expr.getValue(elContext);
			String fullRepoPath = Paths.get(taskExecutionContext.workingDir(), repoPath).toFile().getAbsolutePath();
			console.printLine("\nExport log path is: " + fullRepoPath);

			commands.add("/ExportLog:" + fullRepoPath);
		}

		ProcessBuilder pb = new ProcessBuilder(commands);
		File dir = new File(taskExecutionContext.workingDir(), taskConfig.getValue(Config.WORKINGDIR.paramName()));
		pb.directory(dir);

		if (Boolean.parseBoolean(taskConfig.getValue(Config.PASSENVIRONMENT.paramName()))) {
			taskExecutionContext.console().printLine("Passing environment variables");
			pb.environment().putAll(taskExecutionContext.environment().asMap());
		} else {
			taskExecutionContext.console().printLine("Passing environment variables not set");
		}
		return pb;
	}

	private ELContext createEvaluationContext(EnvironmentVariables envars) {
		final SimpleContext context = new SimpleContext();
		envars.asMap().forEach((k, v) -> context.setVariable(k, factory.createValueExpression(v, String.class)));
		return context;
	}
}
