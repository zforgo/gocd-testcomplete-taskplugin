package hu.zforgo.go.plugin.testcomplete;

import com.thoughtworks.go.plugin.api.response.validation.ValidationError;
import com.thoughtworks.go.plugin.api.task.TaskConfigProperty;

public enum Config {
	EXECUTABLE("Executable") {
		@Override
		public ValidationError validate(Object value) {
			return null;
		}
	},
	PATH("ApplicationPath", System.getenv("SMARTBEAR_HOME")) {
		@Override
		public ValidationError validate(Object value) {
			return null;
		}
	},
	WORKINGDIR("WorkingDirectory") {
		@Override
		public ValidationError validate(Object value) {
			return null;
		}
	},
	SUITE("SuitePath") {
		@Override
		public ValidationError validate(Object value) {
			return null;
		}
	},
	PASSENVIRONMENT("PassEnvironment") {
		@Override
		public ValidationError validate(Object value) {
			return null;
		}
	},
	REPORTPATH("ReportPath") {
		@Override
		public ValidationError validate(Object value) {
			return null;
		}
	};
	private final String paramName;
	private final String defaultValue;

	Config(String paramName) {
		this(paramName, null);
	}

	Config(String paramName, String defaultValue) {
		this.paramName = paramName;
		this.defaultValue = defaultValue;
	}

	public String paramName() {
		return paramName;
	}

	public TaskConfigProperty toProperty() {
		return (TaskConfigProperty) new TaskConfigProperty(paramName, null).withDefault(defaultValue);
	}

	public abstract ValidationError validate(Object value);
}
