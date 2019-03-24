package ru.ancevt.util.ini;

public class IniLine {
	private String key;
	private String value;
	private String comment;

	public IniLine() {

	}

	public IniLine(String key, String data) {
		this();
		this.key = key;
		this.value = data;
	}

	public IniLine(String comment) {
		this();
		this.comment = comment;
	}

	@Override
	protected IniLine clone() throws CloneNotSupportedException {
		final IniLine result = new IniLine();
		
		if (this.isComment())
			result.setComment(this.getComment());
		else {
			result.setKey(this.getKey());
			result.setValue(this.getValue());
		}
		
		return result;
	}

	public final void clear() {
		this.key = this.value = this.comment = new String();
	}

	public final String stringify(char commentChar) {
		IniUtil.validateCommentChar(commentChar);

		final StringBuilder stringBuilder = new StringBuilder();

		if (key != null && value != null)
			stringBuilder.append(key).append(IniChar.EQUALS).append(value);

		if (isComment())
			stringBuilder.append(commentChar).append(IniChar.SPACE).append(getComment());

		return stringBuilder.toString();
	}

	public final String stringify() {
		return stringify(IniChar.COMMENT_DEFAULT);
	}

	public final boolean isComment() {
		return this.comment != null;
	}

	public final boolean isEmpty() {
		return key == null && value == null && comment == null;
	}

	/**
	 * @return the comment
	 */
	public final String getComment() {
		return comment;
	}

	/**
	 * @param comment the comment to set
	 */
	public final void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * @return the key
	 */
	public final String getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public final void setKey(String key) {
		this.key = key;
	}

	/**
	 * @return the data
	 */
	public final String getValue() {
		return value;
	}

	public final String getValue(String defaultValue) {
		final String result = getValue();
		return result == null ? defaultValue : result;
	}

	/**
	 * @param data the data to set
	 */
	public final void setValue(String data) {
		this.value = data;
	}

	public final int getInt() {
		return Integer.valueOf(getValue());
	}

	public final int getInt(int defaultValue) {
		final String result = getValue();
		return result == null ? defaultValue : getInt();
	}

	public final void setInt(int value) {
		setValue(Integer.toString(value));
	}

	public final boolean getBoolean() {
		return Boolean.valueOf(getValue());
	}

	public final boolean getBoolean(boolean defaultValue) {
		final String result = getValue();
		return result == null ? defaultValue : getBoolean();
	}

	public final void setBoolean(boolean value) {
		setValue(Boolean.toString(value));
	}

	public final float getFloat() {
		return Float.valueOf(getValue());
	}

	public final float getFloat(float defaultValue) {
		final String result = getValue();
		return result == null ? defaultValue : getFloat();
	}

	public final void setFloat(float value) {
		setValue(Float.toString(value));
	}

	public final double getDouble() {
		return Double.valueOf(getValue());
	}

	public final double getDouble(double defaultValue) {
		final String result = getValue();
		return result == null ? defaultValue : getDouble();
	}

	public final void setDouble(double value) {
		setValue(Double.toString(value));
	}

	public final long getLong() {
		return Long.valueOf(getValue());
	}

	public final long getLong(long defaultValue) {
		final String result = getValue();
		return result == null ? defaultValue : getLong();
	}

	public final short getShort() {
		return Short.valueOf(getValue());
	}

	public final short getShort(short defaultValue) {
		final String result = getValue();
		return result == null ? defaultValue : getShort();
	}

	public final void setShort(short value) {
		setValue(Short.toString(value));
	}

	public final byte getByte() {
		return Byte.valueOf(getValue());
	}

	public final byte getByte(byte defaultValue) {
		final String result = getValue();
		return result == null ? defaultValue : getByte();
	}

	public final void setByte(byte value) {
		setValue(Byte.toString(value));
	}

	@Override
	public String toString() {
		if(isEmpty()) return ""; else
		if(isComment()) return IniChar.COMMENT_DEFAULT + comment; else
		return String.format("%s=%s", key, value);
	}
}
