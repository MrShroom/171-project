import connectK.BoardModel;

public class EvaluateState 
{
	static private MyLRU<BoardModel, Integer> seenBoards;
	
	
	//Assume 2116026368 bytes for for maxHeap
	//but cache size ~1048576 bytes(we want to fit in cache)
	//and Board model takes ~200 byte
	//and Integer ~4 bytes
	//and references ~ 8bytes 
	//therefore for each add to my tracking 
	//data structs takes ~244 bytes.
	//1048576 / 244 = ~4297
	static final private int maxMapSize = 4000; 
	
	static public void setCompacity()
	{
		seenBoards = new MyLRU<BoardModel, Integer> (maxMapSize);
	}
	
	static public int evaluate(BoardModel state, byte player)
	{
		if(seenBoards.get(state)!= null)
		{
			return seenBoards.get(state);
		}		

		byte otherPlayer = (byte) (player == 1 ? 2 : 1);
		int value = 0;
		if(state.gravity)
		{
			value += pWinningLines(state, player);
			value -= pWinningLines(state, otherPlayer);
		}
		else
		{
			value += pWinningLines(state, player);
			value -= pWinningLines(state, otherPlayer) * state.getkLength();
		}
		
		
		seenBoards.set(state, value);
		return value;		
	}

	private static int pWinningLines(BoardModel state, byte player)
	{
		int currentCount =0;
		int numberSinceFound =0,blanks =0;
		byte otherPlayer = (byte) (player == 1 ? 2 : 1);
		
		//count columns 
		for(int i =0; i < state.getWidth(); i++)
		{			
			numberSinceFound =0;
			blanks = 0;
			for(int j = 0; j < state.getHeight(); j++)
			{
				if(state.getSpace(i, j) == player)
				{
					numberSinceFound++;
				}
				else if(state.getSpace(i, j) == otherPlayer)
				{
					numberSinceFound =0;
					blanks = 0;
				}
				else 
				{
					blanks++;
				}
				if((blanks+numberSinceFound) == state.getkLength() && numberSinceFound > 0 )
				{
					currentCount += numberSinceFound;//Math.pow(4,numberSinceFound);
					break;
				}
			}
		}
		
		//count rows
		for(int j = 0; j < state.getHeight(); j++)
		{
			numberSinceFound =0;
			blanks =0;			
			for(int i =0; i < state.getWidth(); i++){
				if(state.getSpace(i, j) == player)
				{
					numberSinceFound++;
				}
				else if(state.getSpace(i, j) == otherPlayer)
				{
					numberSinceFound =0;
					blanks = 0;
				}
				else 
				{
					blanks++;
				}
				if((blanks+numberSinceFound) == state.getkLength() && numberSinceFound > 0 )
				{
					currentCount += numberSinceFound;//Math.pow(4,numberSinceFound);
					break;
				}
			}
		}
		
		int jj;
		
		for(int j = 0; j < state.getHeight(); j++)
		{
			numberSinceFound =0;
			blanks =0;
			jj = j;
			
			for(int i = 0; i < state.getWidth() && jj < state.getHeight(); i++, jj++){
				if(state.getSpace(i, jj) == player)
				{
					numberSinceFound++;
				}
				else if(state.getSpace(i, jj) == otherPlayer)
				{
					numberSinceFound =0;
					blanks = 0;
				}
				else 
				{
					blanks++;
				}
				if((blanks+numberSinceFound) == state.getkLength() && numberSinceFound > 0 )
				{
					currentCount += numberSinceFound;//Math.pow(4,numberSinceFound);
					break;
				}
			}
		}
		
		int ii;
		for(int i = 1; i < state.getWidth(); i++)
		{
			numberSinceFound =0;
			blanks = 0;
			ii = i;
			
			for(int j = 0; ii < state.getWidth() && j < state.getHeight(); ii++, j++){
				if(state.getSpace(ii, j) == player)
				{
					numberSinceFound++;
				}
				else if(state.getSpace(ii, j) == otherPlayer)
				{
					numberSinceFound =0;
					blanks = 0;
				}
				else 
				{
					blanks++;
				}
				if((blanks+numberSinceFound) == state.getkLength() && numberSinceFound > 0 )
				{
					currentCount += numberSinceFound;//Math.pow(4,numberSinceFound);
					break;
				}
			}
		}
		
		for(int j = state.getHeight()-1; j >= 0 ; j--)
		{
			numberSinceFound =0;
			blanks = 0;
			jj = j;
			
			for(int i = 0; i < state.getWidth() && jj >=0; i++, jj--){
				if(state.getSpace(i, jj) == player)
				{
					numberSinceFound++;
				}
				else if(state.getSpace(i, jj) == otherPlayer)
				{
					numberSinceFound =0;
					blanks =0;
				}
				else 
				{
					blanks++;
				}
				if((blanks+numberSinceFound) == state.getkLength() && numberSinceFound > 0 )
				{
					currentCount += numberSinceFound;//Math.pow(4,numberSinceFound);
					break;
				}
			}
		}
		
		
		for(int i = 1; i < state.getWidth() ; i++)
		{
			numberSinceFound =0;
			blanks = 0;
			ii = i;
			
			for(int j = state.getHeight() - 1; ii < state.getWidth() && j >=0; ii++, j--)
			{
				if(state.getSpace(ii, j) == player)
				{
					numberSinceFound++;
				}
				else if(state.getSpace(ii, j) == otherPlayer)
				{
					numberSinceFound =0;
					blanks =0;
				}
				else 
				{
					blanks++;
				}
				if((blanks+numberSinceFound) == state.getkLength() && numberSinceFound > 0 )
				{
					currentCount += numberSinceFound;//Math.pow(4,numberSinceFound);
					break;
				}
			}
		}		
		return currentCount;
	}
}
