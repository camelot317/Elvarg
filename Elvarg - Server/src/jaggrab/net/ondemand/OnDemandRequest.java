package jaggrab.net.ondemand;

import jaggrab.net.FileDescriptor;

public final class OnDemandRequest implements Comparable<OnDemandRequest> {

	public enum Priority {
		HIGH(1), MEDIUM(2), LOW(3);

		public static Priority valueOf(int v) {
			switch (v) {
			case 1:
				return HIGH;
			case 2:
				return MEDIUM;
			case 3:
				return LOW;
			default:
				throw new IllegalArgumentException("priority out of range");
			}
		}

		private final int intValue;

		private Priority(int intValue) {
			this.intValue = intValue;
		}

		public int toInteger() {
			return intValue;
		}

	}

	private final FileDescriptor fileDescriptor;

	private final Priority priority;

	public OnDemandRequest(FileDescriptor fileDescriptor, Priority priority) {
		this.fileDescriptor = fileDescriptor;
		this.priority = priority;
	}

	public FileDescriptor getFileDescriptor() {
		return fileDescriptor;
	}

	public Priority getPriority() {
		return priority;
	}

	@Override
	public int compareTo(OnDemandRequest o) {
		int thisPriority = priority.toInteger();
		int otherPriority = o.priority.toInteger();

		if (thisPriority < otherPriority) {
			return 1;
		} else if (thisPriority == otherPriority) {
			return 0;
		} else {
			return -1;
		}
	}

}
