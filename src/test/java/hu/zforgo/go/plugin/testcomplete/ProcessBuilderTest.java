package hu.zforgo.go.plugin.testcomplete;

import com.thoughtworks.go.plugin.api.task.Console;
import com.thoughtworks.go.plugin.api.task.EnvironmentVariables;
import de.odysseus.el.ExpressionFactoryImpl;
import de.odysseus.el.util.SimpleContext;
import org.junit.Test;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ProcessBuilderTest {
	final ExpressionFactory factory = new ExpressionFactoryImpl();

	@Test
	public void pathTest() {
		Map<String, String> m = new HashMap<>();
		m.put("GO_STAGE_NAME", "test-control");
		m.put("GO_PIPELINE_COUNTER", "42");
		ELContext context = createEvaluationContext(m);

		final String input = "results/${GO_STAGE_NAME}/testcomplete_${GO_PIPELINE_COUNTER}.mht";
		ValueExpression expr = factory.createValueExpression(context, input, String.class);
		String repoPath = (String) expr.getValue(context);
		System.out.println(repoPath);
	}

	private ELContext createEvaluationContext(Map<String, String> envars) {
		final SimpleContext context = new SimpleContext();
		envars.forEach((k, v) -> context.setVariable(k, factory.createValueExpression(v, String.class)));
		return context;
	}

	//	@Test
	public void test() {
		List<String> commands = new ArrayList<>();

		commands.add("svn");
		commands.add("info");
		ProcessBuilder pb = new ProcessBuilder(commands);
		pb.directory(new File("/work/source/hugo-trunk"));

		Process process = null;
		try {
			process = pb.start();

//			console.readErrorOf(process.getErrorStream());
//			console.readOutputOf(process.getInputStream());

			int exitCode = process.waitFor();

			if (exitCode != 0) {
				System.err.println("Build failure");
				String tmp = printStream(process.getErrorStream());
				System.err.println(printStream(process.getErrorStream()));
			} else {
//				String tmp = printStream(process.getInputStream());
				System.out.println(printStream(process.getInputStream()));
			}
		} catch (Exception e) {
			if (process != null) {
				System.err.println(printStream(process.getErrorStream()));
			}
			System.err.println("Fail: Exception while running TestBuild task: " + e.getMessage());
		} finally {
			if (process != null) {
				process.destroy();
			}

		}

	}

	private String printStream(InputStream is) {
		Scanner s = new Scanner(is).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}

	private String printStream2(InputStream is) {

		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();

		String line;
		try {

			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return sb.toString();

	}

}
