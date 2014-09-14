package tum.dss.thesis.lsframework;

@SuppressWarnings("serial")
public class ItemsetHandlerMock extends AbstractItemsetHandler {
	
	public final int number;
	
	public ItemsetHandlerMock(final int items) {
		number = items;
	}

	@Override
	public boolean isSubset(int k, int j) {
		return (k<j);
	}

	@Override
	public int getNumItemsInSubset(int j) {
		return number;
	}

	@Override
	public int getNumDistinctItems() {
		return 1;
	}

	@Override
	public int getNumSubsets() {
		return number;
	}

	@Override
	public int getNumItems(int type) {
		return number;
	}

	@Override
	public int getNumItems() {
		return number;
	}

	@Override
	public boolean isSuperset(int k, int j) {
		return (k>j);
	}

	@Override
	public String getSet(int j) {
		return Integer.toString(j);
	}

}
