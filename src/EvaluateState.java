import connectK.BoardModel;

public class EvaluateState {
	
	static public int evaluate(BoardModel state, byte player)
	{
		byte otherPlayer = (byte) (player == 1 ? 2 : 1);
		int value = 0;
		value += pWinningLines(state, player);
		value -= pWinningLines(state, otherPlayer);
		return value;
		
	}

	private static int pWinningLines(BoardModel state, byte player)
	{
		int currentCount =0;
		byte otherPlayer = (byte) (player == 1 ? 2 : 1);
		for(int i =0; i < state.getWidth(); i++)
		{
			int numberSinceFound =0;
			for(int j = 0; j < state.getHeight(); j++)
			{
				if(state.getSpace(i, j) == player)
				{
					numberSinceFound++;
				}
				else if(state.getSpace(i, j) == otherPlayer)
				{
					numberSinceFound =0;
				}
				else 
				{
					numberSinceFound++;
				}
				if( numberSinceFound == state.getkLength())
				{
					currentCount++;
					break;
				}
			}
		}
		
		for(int j = 0; j < state.getHeight(); j++)
		{
			int numberSinceFound =0;
			
			for(int i =0; i < state.getWidth(); i++){
				if(state.getSpace(i, j) == player)
				{
					numberSinceFound++;
				}
				else if(state.getSpace(i, j) == otherPlayer)
				{
					numberSinceFound =0;
				}
				else 
				{
					numberSinceFound++;
				}
				if( numberSinceFound == state.getkLength())
				{
					currentCount++;
					break;
				}
			}
		}
		return currentCount;
	}
}
