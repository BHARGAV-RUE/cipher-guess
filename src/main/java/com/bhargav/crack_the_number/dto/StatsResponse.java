package com.bhargav.crack_the_number.dto;

public class StatsResponse {
    private int totalGames;
    public int getTotalGames(){
        return totalGames;
    }
    public void setTotalGames(int totalGames){
        this.totalGames = totalGames;
    }

    private int totalWins;
    public int getTotalWins(){
        return totalWins;
    }
    public void setTotalWins(int totalWins){
        this.totalWins = totalWins;
    }
    private int totalLosses;
    public int getTotalLosses(){
        return totalLosses;
    }
    public void setTotalLosses(int totalLosses){
        this.totalLosses = totalLosses;
    }
    private double winRate;
    public double getWinRate(){
        return winRate;
    }
    public void setWinRate(double winRate){
        this.winRate = winRate;
    }

    private int bestScore;
    public int getBestScore(){
        return bestScore;
    }
    public void setBestScore(int bestScore){
        this.bestScore = bestScore;
    }


}
