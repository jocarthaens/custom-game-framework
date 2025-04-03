package utils;

public interface Copyable<T extends Copyable<? extends T>> {

	public abstract T copy();
}
