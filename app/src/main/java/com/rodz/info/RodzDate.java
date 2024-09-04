package com.rodz.info;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class RodzDate {
    long left;
    public long timestamp;
    public int day, month, year, hour, minutes, seconds;
    String[] days = new String[] {"Sun", "Mon", "Tues", "Wed", "Thur", "Fri", "Sat"};
    String[] months = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    int[] month_days = new int[] {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};


    public RodzDate(){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        Date date = new Date();
        String dateString = formatter.format(date);

        String[] dchars = dateString.split(" ");

        String[] chars = dchars[0].split("/");
        day = Integer.parseInt(chars[0]);
        month = Integer.parseInt(chars[1]);
        year = Integer.parseInt(chars[2]);

        //do for time
        String[] tchars = dchars[1].split(":");
        hour = Integer.parseInt(tchars[0]);
        minutes = Integer.parseInt(tchars[1]);
        seconds = Integer.parseInt(tchars[2]);
    }

    public RodzDate(String timestamp){
        left = Integer.parseInt(timestamp);
        this.timestamp = left;

        year = 1970;
        boolean can = true;
        while (can){
            int secondsYear = 0;
            if (isLeapYear(year)){
                secondsYear = (24 * 60 * 60) * 366;
            }
            else{
                secondsYear = (24 * 60 * 60) * 365;
            }

            if (left > secondsYear){
                left = left - secondsYear;
                year += 1;
            }
            else{
                can = false;
            }
        }

        left += (24 * 60 * 60) + (3600*2);
        month = 0;
        //year= 2022;

        boolean done = true;
        for (int i = 0; i < month_days.length; i++){
            if (done) {
                int month_secs = month_days[i] * (24 * 60 * 60);
                if (left > month_secs) {
                    left = left - month_secs;
                } else {
                    month = i + 1;
                    done = false;
                }
            }
        }
        //System.out.println(left);
        day = (int)(left / (24 * 60 * 60));

        left = left - (day * (24 * 60 * 60));

        hour = (int)(left/3600);

        left = left - (hour * 3600);

        minutes = (int)(left / 60);

        seconds = (int) (left - (minutes * 60));
    }

    public boolean isLeapYear(int num){
        double res = Double.valueOf(String.valueOf(num)) / 4;
        double num2 = Math.floor(res);
        double dif = res - num2;
        //System.out.println("Out:"+dif+","+res);

        if (dif == 0.0){
            return true;
        }
        else{
            return false;
        }
    }

    public RodzDate(int year, int month, int day){
        this.day = day;
        this.month = month;
        this.year = year;
        hour = 0;
        minutes = 0;
        seconds = 0;
    }

    public RodzDate(int year, int month, int day, int hour, int minutes, int seconds){
        this.day = day;
        this.month = month;
        this.year = year;
        this.hour = hour;
        this.minutes = minutes;
        this.seconds = seconds;
    }

    public int getDay(){
        return  day;
    }

    public String getDayName(){
        int m = month-2;
        int year = m<1?this.year-1:this.year;
        m = m<1?(10-m):m;

        int firstNumbers = Integer.parseInt(String.valueOf(year).substring(0,2));
        int lastNumbers = Integer.parseInt(String.valueOf(year).substring(2));

        //k is  the day of the month.
        //m is the month number.
        //D is the last two digits of the year.
        //C is the first two digits of the year.
        double k = day;
        double D = lastNumbers;
        double C = firstNumbers;

        double F= k+ (Math.floor(((13*m)-1)/5)) +D+ Math.floor(D/4) +Math.floor(C/4)-2*C;

        double div = (F/7);
        double rem = Math.round((div - Math.floor(div)) * 7);
        int df = (int)rem;
        String[] days = new String[]{"Sun", "Mon", "Tues", "Wed", "Thu", "Fri", "Sat","Out", "Out2"};
        System.out.println("Day index was: "+df);

        df = df<0?0:df>7?7:df;


        return days[df];
    }

    public int getDayOfMonth(){
        return  day;
    }

    public int getMonth(){
        return  month;
    }

    public int getYear(){
        return  year;
    }

    public int getDayOfYear(){
        int day = 0;

        for (int i = 1; i < month; i++){
            day += month_days[i];
        }

        return day + this.day;
    }

    public String getMonthName(){
        String name = "";
        return months[month-1];
    }

    public int getHour() {
        return hour;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getSeconds() {
        return seconds;
    }

    public int getTimeStampDay(){
        int stamp = 0;
        stamp = (hour * 3600) + (minutes * 60) + seconds;
        return stamp;
    }

    public long getTimeStamp(){
        long time2 = 0;

        //loop through years
        int year = 1970;
        boolean can = true;
        while (can){
            if (this.year != year) {
                int secondsYear = 0;
                if (isLeapYear(year)) {
                    secondsYear = (24 * 60 * 60) * 366;
                } else {
                    secondsYear = (24 * 60 * 60) * 365;
                }
                time2 += secondsYear;
                year += 1;
            }
            else{
                can = false;
            }
        }
        //loop through months
        for (int i = 0; i < month-1; i++ ){
            time2 += month_days[i] * (24 * 60 * 60);
        }

        //add days
        time2 += (day-1) * (24 * 60 * 60);

        //then hour, minutes and seconds
        time2 += (hour * 3600) + (minutes * 60) + seconds;
        return time2;
    }

    public String getFullDate(){
        return getDay()+" "+getMonthName()+" "+getYear()+", "+getHour()+":"+getMinutes()+":"+getSeconds();
    }
}
