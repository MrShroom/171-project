import connectK.BoardModel;

public class EvaluateState 
{
	static private MyLRU<BoardModel, Double> seenBoards;

	static final private int maxMapSize = 4000; 
	static final private double base = 10.0;
	static final private double bias = 0.85;	
	static private int possibleWin[][];
	static private int possibleLoses[][];
	
	static public void setCompacity()
	{
		seenBoards = new MyLRU<BoardModel, Double> (maxMapSize);
	}
	
	static public double evaluate(BoardModel state, byte player)
	{
		if(seenBoards.containsKey(state))
		{
			return seenBoards.get(state);
		}			
    
		double value = 0;
		pWinningLines(state, player);
		
		for (int j = 0; j < 4; j++) 
		{
		  for (int i = 1; i < state.kLength ; i++) 
		  {
			  value += (possibleWin[j][i] * Math.pow(base, i)* bias);
			  value -= (possibleLoses[j][i] * Math.pow(base, i));
		  }
		}	
		seenBoards.put(state, value);
		return value;		
	}

	private static void reset(BoardModel state)
	{
		
		possibleWin = new int [4][state.getkLength()];
		possibleLoses = new int [4][state.getkLength()];
		for(int j = 0; j < 4; j++)
		{
			for(int i = 0; i < state.getkLength(); i++)
			{
				possibleWin[j][i] =0;
				possibleLoses[j][i] =0;
			}
		}
	}
	private static void pWinningLines(BoardModel state, byte player)
	{
		int numberPlayerFound = 0,numberOtherPlayerFound = 0;
		byte otherPlayer = (byte) (player == 1 ? 2 : 1);
		reset(state);
		//count columns 
		for(int i =0; i < state.getWidth(); i++)
		{
			for(int j = 0; j < state.getHeight() - state.kLength; j++)
			{
				for(int l = j; l < j + state.kLength ; l++)
				{
					if(state.getSpace(i, l) == player)
					{
						numberPlayerFound++;
					}
					else if(state.getSpace(i, l) == otherPlayer)
					{
						numberOtherPlayerFound++;
					}
				}
				if (numberPlayerFound == 0) 
			    {
					
					possibleLoses[0][numberOtherPlayerFound]++;
		        }
				else if(numberOtherPlayerFound == 0)
				{
					possibleWin[0][numberPlayerFound]++;
				}
				numberPlayerFound =0;
				numberOtherPlayerFound =0;
			}
		}
			
		//count rows
		for(int j = 0; j < state.getHeight(); j++)
		{
			for(int i =0; i < state.getWidth() - state.kLength; i++)
			{				
				for(int l = i; l < i + state.kLength; l++)
				{
					if(state.getSpace(l,j) == player)
					{
						numberPlayerFound++;
					}
					else if(state.getSpace(l,j) == otherPlayer)
					{
						numberOtherPlayerFound++;
					}
				}
				if (numberPlayerFound == 0) 
			    {					
					possibleLoses[1][numberOtherPlayerFound]++;
		        }
				else if(numberOtherPlayerFound == 0)
				{
					possibleWin[1][numberPlayerFound]++;
				}
				numberPlayerFound =0;
				numberOtherPlayerFound =0;
			}
		}
		
		//up right or down left diagonal. 
		int jj;		
		int howhigh = state.getHeight()- state.kLength +1;
		int howlong = state.getWidth() - state.kLength +1;
		for(int j = 0; j < howhigh; j++)
		{	
			jj = j;			
			for(int i = 0; i < howlong && jj < howhigh; i++, jj++)
			{
				for(int l = i, ll = jj; l < i + state.kLength && ll < jj + state.kLength; l++, ll++)
				{
					if(state.getSpace(l,ll) == player)
					{
						numberPlayerFound++;
					}
					else if(state.getSpace(l,ll) == otherPlayer)
					{
						numberOtherPlayerFound++;
					}
				}
				if (numberPlayerFound == 0) 
			    {
					
					possibleLoses[2][numberOtherPlayerFound]++;
		        }
				else if(numberOtherPlayerFound == 0)
				{
					possibleWin[2][numberPlayerFound]++;
				}
				numberPlayerFound =0;
				numberOtherPlayerFound =0;
			}
				
		}
		
		//up right or down left diagonal.(cont.)
		int ii;
		for(int i = 1; i < howlong; i++)
		{
			ii = i;			
			for(int j = 0; ii < howlong && j < howhigh; ii++, j++)
			{
				for(int l = ii, ll = j; l < ii + state.kLength && ll < j + state.kLength; l++, ll++)
				{
					if(state.getSpace(l,ll) == player)
					{
						numberPlayerFound++;
					}
					else if(state.getSpace(l,ll) == otherPlayer)
					{
						numberOtherPlayerFound++;
					}
				}
				if (numberPlayerFound == 0) 
			    {
					
					possibleLoses[2][numberOtherPlayerFound]++;
		        }
				else if(numberOtherPlayerFound == 0)
				{
					possibleWin[2][numberPlayerFound]++;
				}
				numberPlayerFound =0;
				numberOtherPlayerFound =0;
			}
		}
		
		//up left or down right diagonal.		
		for(int j = state.getHeight()-1; j >= state.kLength ; j--)
		{
			jj = j;			
			for(int i = 0; i < howlong && jj >=state.kLength; i++, jj--)
			{
				for(int l = i, ll = jj; l < i + state.kLength && ll > jj - state.kLength; l++, ll--)
				{
					if(state.getSpace(l,ll) == player)
					{
						numberPlayerFound++;
					}
					else if(state.getSpace(l,ll) == otherPlayer)
					{
						numberOtherPlayerFound++;
					}
				}
				if (numberPlayerFound == 0) 
			    {
					
					possibleLoses[3][numberOtherPlayerFound]++;
		        }
				else if(numberOtherPlayerFound == 0)
				{
					possibleWin[3][numberPlayerFound]++;
				}
				numberPlayerFound =0;
				numberOtherPlayerFound =0;	
			}
		}
		
		//up left or down right diagonal(cont.)
		for(int i = 1; i < howlong ; i++)
		{
			ii = i;
			for(int j = state.getHeight() - 1; ii < howlong && j >state.kLength; ii++, j--)
			{
				for(int l = i, ll = j; l < i + state.kLength && ll > j-state.kLength; l++, ll--)
				{
					if(state.getSpace(l,ll) == player)
					{
						numberPlayerFound++;
					}
					else if(state.getSpace(l,ll) == otherPlayer)
					{
						numberOtherPlayerFound++;
					}
				}
				if (numberPlayerFound == 0) 
			    {
					
					possibleLoses[3][numberOtherPlayerFound]++;
		        }
				else if(numberOtherPlayerFound == 0)
				{
					possibleWin[3][numberPlayerFound]++;
				}
				numberPlayerFound =0;
				numberOtherPlayerFound =0;	
			}
		}
	}

}
