package my.springframework.messaging;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.util.AlternativeJdkIdGenerator;
import org.springframework.util.IdGenerator;

public class MessageHeaders implements Map<String, Object>, Serializable {

	/**
	 * The key for the Message ID. This is an automatically generated UUID and
	 * should never be explicitly set in the header map <b>except</b> in the
	 * case of Message deserialization where the serialized Message's generated
	 * UUID is being restored.
	 */
	public static final String ID = "id";

	public static final String REPLY_CHANNEL = "replyChannel";

	private static final long serialVersionUID = 7035068984263400920L;

	private static final IdGenerator defaultIdGenerator = new AlternativeJdkIdGenerator();

	private final Map<String, Object> headers;

	public MessageHeaders(Map<String, Object> headers) {
		this.headers = (headers != null ? new HashMap<String, Object>(headers) : new HashMap<String, Object>());
		this.headers.put(ID, defaultIdGenerator.generateId());
	}

	public UUID getId() {
		return get(ID, UUID.class);
	}

	public Object getReplyChannel() {
		return get(REPLY_CHANNEL);
	}

	@SuppressWarnings("unchecked")
	public <T> T get(Object key, Class<T> type) {
		Object value = this.headers.get(key);
		if (value == null) {
			return null;
		}
		if (!type.isAssignableFrom(value.getClass())) {
			throw new IllegalArgumentException("Incorrect type specified for header '" +
					key + "'. Expected [" + type + "] but actual type is [" + value.getClass() + "]");
		}
		return (T) value;
	}


	// Delegating Map implementation

	public boolean containsKey(Object key) {
		return this.headers.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return this.headers.containsValue(value);
	}

	public Set<Map.Entry<String, Object>> entrySet() {
		return Collections.unmodifiableMap(this.headers).entrySet();
	}

	public Object get(Object key) {
		return this.headers.get(key);
	}

	public boolean isEmpty() {
		return this.headers.isEmpty();
	}

	public Set<String> keySet() {
		return Collections.unmodifiableSet(this.headers.keySet());
	}

	public int size() {
		return this.headers.size();
	}

	public Collection<Object> values() {
		return Collections.unmodifiableCollection(this.headers.values());
	}


	// Unsupported Map operations

	/**
	 * Since MessageHeaders are immutable, the call to this method
	 * will result in {@link UnsupportedOperationException}.
	 */
	public Object put(String key, Object value) {
		throw new UnsupportedOperationException("MessageHeaders is immutable");
	}

	/**
	 * Since MessageHeaders are immutable, the call to this method
	 * will result in {@link UnsupportedOperationException}.
	 */
	public void putAll(Map<? extends String, ? extends Object> map) {
		throw new UnsupportedOperationException("MessageHeaders is immutable");
	}

	/**
	 * Since MessageHeaders are immutable, the call to this method
	 * will result in {@link UnsupportedOperationException}.
	 */
	public Object remove(Object key) {
		throw new UnsupportedOperationException("MessageHeaders is immutable");
	}

	/**
	 * Since MessageHeaders are immutable, the call to this method
	 * will result in {@link UnsupportedOperationException}.
	 */
	public void clear() {
		throw new UnsupportedOperationException("MessageHeaders is immutable");
	}


	// equals, hashCode, toString

	@Override
	public boolean equals(Object other) {
		return (this == other ||
				(other instanceof MessageHeaders && this.headers.equals(((MessageHeaders) other).headers)));
	}

	@Override
	public int hashCode() {
		return this.headers.hashCode();
	}

	@Override
	public String toString() {
		return this.headers.toString();
	}

}
