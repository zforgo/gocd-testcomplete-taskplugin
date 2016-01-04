package hu.zforgo.go.plugin.testcomplete;

import com.thoughtworks.go.plugin.api.annotation.Extension;
import com.thoughtworks.go.plugin.api.response.validation.ValidationError;
import com.thoughtworks.go.plugin.api.response.validation.ValidationResult;
import com.thoughtworks.go.plugin.api.task.Task;
import com.thoughtworks.go.plugin.api.task.TaskConfig;
import com.thoughtworks.go.plugin.api.task.TaskExecutor;
import com.thoughtworks.go.plugin.api.task.TaskView;
import org.apache.commons.io.IOUtils;

@Extension
public class TestCompleteTask implements Task {

	@Override
	public TaskConfig config() {
		TaskConfig config = new TaskConfig();
		for (Config c : Config.values()) {
			config.add(c.toProperty());
		}
		return config;
	}

	@Override
	public TaskExecutor executor() {
		return new TestCompleteTaskExecutor();
	}

	@Override
	public TaskView view() {
		return new TaskView() {
			@Override
			public String displayValue() {
				return "SmartBear Testing Tool";
			}

			@Override
			public String template() {
				try {
					return IOUtils.toString(getClass().getResourceAsStream("/views/task.template.html"), "UTF-8");
				} catch (Exception e) {
					return "Failed to find template: " + e.getMessage();
				}
			}
		};
	}

	@Override
	public ValidationResult validate(TaskConfig taskConfig) {
		ValidationResult validationResult = new ValidationResult();
		for (Config c : Config.values()) {
			ValidationError err = c.validate(taskConfig.getValue(c.paramName()));
			if (err != null) {
				validationResult.addError(err);
			}
		}
		return validationResult;
	}
}
