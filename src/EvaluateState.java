import connectK.BoardModel;

public class EvaluateState 
{
	static private MyLRU<BoardModel, Long> seenBoards;

	static final private int maxMapSize = 700000; 
	static final private double base = 4.0;
	static private double playerBias = .75;
	static final private double diagonalBias = 1.05;
	static private int possibleWin[][];
	static private int possibleLoses[][];
	static private int longestDia;
	
	static public void setCompacity(BoardModel state, byte player)
	{
		seenBoards = new MyLRU<BoardModel, Long> (maxMapSize);
		if ( player == 1)
			playerBias = .75;
		else 
			playerBias = .50;
	}
	
	static public long evaluate(BoardModel state, byte player)
	{
		if(seenBoards.containsKey(state))
		{
			return seenBoards.get(state);
		}			
    
		long value = 0;
		pWinningLines(state, player);
		
		for (int j = 0; j < 2; j++) 
		{
		  for (int i = 1; i < longestDia ; i++) 
		  {
			  value += (possibleWin[j][i] * Math.pow(base, i)* playerBias);
			  value -= (possibleLoses[j][i] * Math.pow(base, i));
		  }
		}
		for (int j = 2; j < 4; j++) 
		{
		  for (int i = 1; i < longestDia ; i++) 
		  {
			  value += (possibleWin[j][i] * Math.pow(base, i)* playerBias *diagonalBias);
			  value -= (possibleLoses[j][i] * Math.pow(base, i) * diagonalBias);
		  }
		}	
		seenBoards.put(state, value);
		return value;		
	}

	private static void reset(BoardModel state)
	{
		longestDia = (int)(Math.sqrt(Math.pow(state.getHeight(),2))+ Math.pow(state.getWidth(),2)) + 1;
		possibleWin = new int [4][longestDia];
		possibleLoses = new int [4][longestDia];
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
		int numberPlayerFound = 0;
		int numberOtherPlayerFound = 0;
		int blanks = 0;
		boolean me = true;
		byte otherPlayer = (byte) (player == 1 ? 2 : 1);
		reset(state);
		
		//count columns 
		for(int i =0; i < state.getWidth(); i++)
		{
			for(int j = 0; j < state.getHeight(); j++)
			{				
				if(state.getSpace(i, j) == player)
				{
					if (!me && numberOtherPlayerFound >0)
					{
						blanks = 0;
						numberOtherPlayerFound =0;
					}
					me = true;
					numberPlayerFound++;
				}
				else if(state.getSpace(i, j) == otherPlayer)
				{
					if (me && numberPlayerFound>0)
					{
						blanks = 0;
						numberPlayerFound =0;
					}
					me = false;
					numberOtherPlayerFound++;
				}	
				else 
				{
					blanks++;
				}
				if(blanks + numberPlayerFound >= state.getkLength()
						|| blanks + numberOtherPlayerFound >= state.getkLength())
					break;
				
			}
			if ((numberOtherPlayerFound + blanks) >= state.getkLength() ) 
		    {				
				possibleLoses[0][numberOtherPlayerFound]++;
	        }
			else if((numberPlayerFound + blanks) >= state.getkLength())
			{
				possibleWin[0][numberPlayerFound]++;
			}
			numberPlayerFound =0;
			numberOtherPlayerFound =0;
			blanks = 0;
		}
			
		//count rows
		for(int j = 0; j < state.getHeight(); j++)
		{
			for(int i =0; i < state.getWidth(); i++)
			{
					if(state.getSpace(i, j) == player)
					{
						if (!me && numberOtherPlayerFound >0)
						{
							blanks = 0;
							numberOtherPlayerFound =0;
						}
						me = true;
						numberPlayerFound++;
					}
					else if(state.getSpace(i, j) == otherPlayer)
					{
						if (me && numberPlayerFound>0)
						{
							blanks = 0;
							numberPlayerFound =0;
						}
						me = false;
						numberOtherPlayerFound++;
					}	
					else 
					{
						blanks++;
					}
					if(blanks + numberPlayerFound >= state.getkLength()
							|| blanks + numberOtherPlayerFound >= state.getkLength())
						break;
		
			}
			if ((numberOtherPlayerFound + blanks) >= state.getkLength() ) 
		    {
				
				possibleLoses[1][numberOtherPlayerFound]++;
	        }
			else if((numberPlayerFound + blanks) >= state.getkLength())
			{
				possibleWin[1][numberPlayerFound]++;
			}
			numberPlayerFound =0;
			numberOtherPlayerFound =0;
			blanks = 0;
		}
		
		//up right or down left diagonal. 
		int jj;
		for(int j = 0; j < state.getHeight(); j++)
		{	
			jj = j;			
			for(int i = 0; i < state.getWidth() && jj < state.getHeight(); i++, jj++)
			{
				
				if(state.getSpace(i, jj) == player)
				{
					if (!me && numberOtherPlayerFound >0)
					{
						blanks = 0;
						numberOtherPlayerFound =0;
					}
					me = true;
					numberPlayerFound++;
				}
				else if(state.getSpace(i, jj) == otherPlayer)
				{
					if (me && numberPlayerFound>0)
					{
						blanks = 0;
						numberPlayerFound =0;
					}
					me = false;
					numberOtherPlayerFound++;
				}	
				else 
				{
					blanks++;
				}
				if(blanks + numberPlayerFound >= state.getkLength()
						|| blanks + numberOtherPlayerFound >= state.getkLength())
					break;
			}				
			
			if ((numberOtherPlayerFound + blanks) >= state.getkLength() ) 
		    {
				
				possibleLoses[2][numberOtherPlayerFound]++;
	        }
			else if((numberPlayerFound + blanks) >= state.getkLength())
			{
				possibleWin[2][numberPlayerFound]++;
			}
			numberPlayerFound =0;
			numberOtherPlayerFound =0;
			blanks = 0;
				
		}
		
		//up right or down left diagonal.(cont.)
		int ii;
		for(int i = 1; i < state.getWidth(); i++)
		{
			ii = i;			
			for(int j = 0; ii < state.getWidth() && j < state.getHeight(); ii++, j++)
			{
				if(state.getSpace(ii, j) == player)
				{
					if (!me && numberOtherPlayerFound >0)
					{
						blanks = 0;
						numberOtherPlayerFound =0;
					}
					me = true;
					numberPlayerFound++;
				}
				else if(state.getSpace(ii, j) == otherPlayer)
				{
					if (me && numberPlayerFound>0)
					{
						blanks = 0;
						numberPlayerFound =0;
					}
					me = false;
					numberOtherPlayerFound++;
				}	
				else 
				{
					blanks++;
				}
				if(blanks + numberPlayerFound >= state.getkLength()
						|| blanks + numberOtherPlayerFound >= state.getkLength())
					break;
			}
			if ((numberOtherPlayerFound + blanks) >= state.getkLength() ) 
		    {
				
				possibleLoses[2][numberOtherPlayerFound]++;
	        }
			else if((numberPlayerFound + blanks) >= state.getkLength())
			{
				possibleWin[2][numberPlayerFound]++;
			}
			numberPlayerFound =0;
			numberOtherPlayerFound =0;
			blanks = 0;
		}
		
		//up left or down right diagonal.		
		for(int j = state.getHeight()-1; j >= 0 ; j--)
		{
			jj = j;			
			for(int i = 0; i < state.getWidth() && jj >=0; i++, jj--)
			{
				if(state.getSpace(i, jj) == player)
				{
					if (!me && numberOtherPlayerFound >0)
					{
						blanks = 0;
						numberOtherPlayerFound =0;
					}
					me = true;
					numberPlayerFound++;
				}
				else if(state.getSpace(i, jj) == otherPlayer)
				{
					if (me && numberPlayerFound>0)
					{
						blanks = 0;
						numberPlayerFound =0;
					}
					me = false;
					numberOtherPlayerFound++;
				}	
				else 
				{
					blanks++;
				}
				if(blanks + numberPlayerFound >= state.getkLength()
						|| blanks + numberOtherPlayerFound >= state.getkLength())
					break;
			}
			if ((numberOtherPlayerFound + blanks) >= state.getkLength() ) 
		    {				
				possibleLoses[3][numberOtherPlayerFound]++;
	        }
			else if((numberPlayerFound + blanks) >= state.getkLength())
			{
				possibleWin[3][numberPlayerFound]++;
			}
			numberPlayerFound =0;
			numberOtherPlayerFound =0;
			blanks = 0;
		}
		
		//up left or down right diagonal(cont.)
		for(int i = 1; i < state.getWidth() ; i++)
		{
			ii = i;
			for(int j = state.getHeight() - 1; ii < state.getWidth() && j >=0; ii++, j--)
			{
				if(state.getSpace(ii, j) == player)
				{
					if (!me && numberOtherPlayerFound >0)
					{
						blanks = 0;
						numberOtherPlayerFound =0;
					}
					me = true;
					numberPlayerFound++;
				}
				else if(state.getSpace(ii, j) == otherPlayer)
				{
					if (me && numberPlayerFound>0)
					{
						blanks = 0;
						numberPlayerFound = 0;
					}
					me = false;
					numberOtherPlayerFound++;
				}	
				else 
				{
					blanks++;
				}	
				if(blanks + numberPlayerFound >= state.getkLength()
						|| blanks + numberOtherPlayerFound >= state.getkLength())
					break;
			}
			if ((numberOtherPlayerFound + blanks) >= state.getkLength() ) 
		    {				
				possibleLoses[3][numberOtherPlayerFound]++;
	        }
			else if((numberPlayerFound + blanks) >= state.getkLength())
			{
				possibleWin[3][numberPlayerFound]++;
			}
			numberPlayerFound =0;
			numberOtherPlayerFound =0;
			blanks = 0;
		}
	}

}
