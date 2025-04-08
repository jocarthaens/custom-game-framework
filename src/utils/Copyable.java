package utils;

// Interface used for providing logical copies of an instance object

public interface Copyable<T extends Copyable<? extends T>> {

	public abstract T copy();
}
