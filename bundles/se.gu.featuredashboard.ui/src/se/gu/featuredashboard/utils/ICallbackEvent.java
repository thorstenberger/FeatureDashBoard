package se.gu.featuredashboard.utils;

public class ICallbackEvent {

	private EventType type;
	private Object data;
	
	public enum EventType {
		ParsingComplete, ChangeDetected
	}
	
	public ICallbackEvent(EventType type) {
		this.type = type;
	}
	
	public ICallbackEvent(EventType type, Object data) {
		this.type = type;
		this.data = data;
	}
	
	public EventType getEventType() {
		return type;
	}
	
	public Object getData() {
		return data;
	}
	
}
