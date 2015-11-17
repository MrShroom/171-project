import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import connectK.BoardModel;

public class EvaluateState 
{
	static public Map<BoardModel, Integer> seenBoards;
	static public Deque<BoardModel> myQueue;
	static public Map<BoardModel, Iterator<BoardModel>> tracker;
	
	
	//Assume 2116026368 bytes for for maxHeap
	//but cache size ~1048576 bytes(we want to fit in cache)
	//and Board model takes ~200 byte
	//and Integer ~4bytes
	//and references ~ 8bytes 
	//therefore for each add to my tracking 
	//data structs takes ~244 bytes.
	//1048576 / 244 = ~4297
	// round down to 4200
	static public int maxMapSize = 4000; 
	
	static public void setCompacity()
	{
		//maxMapSize= (int) Math.pow(state.getHeight()*state.getWidth(),3);
		seenBoards = new HashMap<BoardModel, Integer> (maxMapSize);
		tracker = new HashMap<BoardModel, Iterator<BoardModel>>(maxMapSize);
		myQueue = new LinkedList<BoardModel>();
	}
	
	static public int evaluate(BoardModel state, byte player)
	{
		if(seenBoards.containsKey(state))
			return seenBoards.get(state);
		
		if (seenBoards.size() >= maxMapSize)
		{
			BoardModel removeMe = myQueue.poll();
			seenBoards.remove(removeMe);
			tracker.remove(removeMe);
		}
		byte otherPlayer = (byte) (player == 1 ? 2 : 1);
		int value = 0;
		if(player == 1)
		{
			value += pWinningLines(state, player) * 1.5;
			value -= pWinningLines(state, otherPlayer);
		}
		else
		{
			value += pWinningLines(state, player) ;
			value -= pWinningLines(state, otherPlayer)* 1.5;
		}
		if(tracker.containsKey(state))
		{
			tracker.get(state).remove();
		}
		seenBoards.put(state, value);
		myQueue.add(state);
		tracker.put(state, myQueue.descendingIterator());
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
