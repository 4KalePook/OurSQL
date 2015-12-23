package table;

public enum Action {
	RESTRICT(true, false), CASCADE(false, true);

	boolean reject, doUpdate;
	Action(boolean reject, boolean doUpdate) {
		this.reject = reject;
		this.doUpdate = doUpdate;
	}

	public static Action getValue(String s) {
		for(Action action: values())
			if( action.name() == s )
				return action;
		return CASCADE;
	}
}
