import java.util.HashMap;
import java.util.Map;

import connectK.BoardModel;

public class EvaluateState 
{
	static public Map<BoardModel, Integer> seenBoards;
	static public int maxMapSize; 
	
	static public void setCompacity(BoardModel state)
	{
		maxMapSize= (int) Math.pow(state.getHeight()*state.getWidth(),2);
		seenBoards = new HashMap<BoardModel, Integer> (maxMapSize);
	}
	
	static public int evaluate(BoardModel state, byte player)
	{
		if (seenBoards.size() == maxMapSize)
		{
			seenBoards.clear();
			System.out.println("states Cleareed");
		}
		if(!seenBoards.containsKey(state))
		{
			byte otherPlayer = (byte) (player == 1 ? 2 : 1);
			int value = 0;
			value += pWinningLines(state, player);
			value -= pWinningLines(state, otherPlayer);
			seenBoards.put(state, value);
		}
		return seenBoards.get(state);		
	}

	private static int pWinningLines(BoardModel state, byte player)
	{
		int currentCount =0;
		byte otherPlayer = (byte) (player == 1 ? 2 : 1);
		
		//count columns 
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
		
		//count rows
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
					numberSinceFound = 0;
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
			int jj = j;
			
			for(int i = 0; i < state.getWidth() && jj < state.getHeight(); i++, jj++){
				if(state.getSpace(i, jj) == player)
				{
					numberSinceFound++;
				}
				else if(state.getSpace(i, jj) == otherPlayer)
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
		
		
		for(int i = 1; i < state.getWidth(); i++)
		{
			int numberSinceFound =0;
			int ii = i;
			
			for(int j = 0; ii < state.getWidth() && j < state.getHeight(); ii++, j++){
				if(state.getSpace(ii, j) == player)
				{
					numberSinceFound++;
				}
				else if(state.getSpace(ii, j) == otherPlayer)
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
		
		for(int j = state.getHeight()-1; j >= 0 ; j--)
		{
			int numberSinceFound =0;
			int jj = j;
			
			for(int i = 0; i < state.getWidth() && jj >=0; i++, jj--){
				if(state.getSpace(i, jj) == player)
				{
					numberSinceFound++;
				}
				else if(state.getSpace(i, jj) == otherPlayer)
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
		
		
		for(int i = state.getWidth()-1; i >= 0 ; i--)
		{
			int numberSinceFound =0;
			int ii = i;
			
			for(int j = 0; ii >= 00 && j < state.getHeight(); ii--, j++){
				if(state.getSpace(ii, j) == player)
				{
					numberSinceFound++;
				}
				else if(state.getSpace(ii, j) == otherPlayer)
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
