abstract class HasherBase<T>
{
	public abstract int getHash(T item, int mod_by);
}
