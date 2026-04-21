package com.bhargav.crack_the_number.dto;

public class StatsResponse {
    //Total games played
    private int totalGames;
    public int getTotalGames(){
        return totalGames;
    }
    public void setTotalGames(int totalGames){
        this.totalGames = totalGames;
    }

    //Total games won
    private int totalWins;
    public int getTotalWins(){
        return totalWins;
    }
    public void setTotalWins(int totalWins){
        this.totalWins = totalWins;
    }

    //Total games lost
    private int totalLosses;
    public int getTotalLosses(){
        return totalLosses;
    }
    public void setTotalLosses(int totalLosses){
        this.totalLosses = totalLosses;
    }

    //Win rate which is total_games_won by total_games_played
    private double winRate;
    public double getWinRate(){
        return winRate;
    }
    public void setWinRate(double winRate){
        this.winRate = winRate;
    }

    //Least guesses taken to end the game
    private int bestScore;
    public int getBestScore(){
        return bestScore;
    }
    public void setBestScore(int bestScore){
        this.bestScore = bestScore;
    }


}
