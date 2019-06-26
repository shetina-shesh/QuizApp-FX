package Server;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Time {
    int hoursFrom;
    int hoursTo;
    int minsFrom;
    int minsTo;

    public Time(int hoursFrom, int hoursTo, int minsFrom, int minsTo) {
        this.hoursFrom = hoursFrom;
        this.hoursTo = hoursTo;
        this.minsFrom = minsFrom;
        this.minsTo = minsTo;
    }

    public boolean currentTimeInRange(){
            try {
                Date currentTime = new Date(System.currentTimeMillis());
                String timeStringFrom = hoursFrom+":"+minsFrom+":00";
                Date timeFrom = new SimpleDateFormat("HH:mm:ss").parse(timeStringFrom);
                Calendar calendarFrom = Calendar.getInstance();
                calendarFrom.setTime(timeFrom);

                String timeStringTo = hoursTo+":"+minsTo+":00";
                Date timeTo = new SimpleDateFormat("HH:mm:ss").parse(timeStringTo);
                Calendar calendarTo = Calendar.getInstance();
                calendarTo.setTime(timeTo);
                calendarTo.add(Calendar.DATE, 1);


                String currentTimeString = currentTime.getHours()+":"+currentTime.getMinutes()+":00";
                Date current = new SimpleDateFormat("HH:mm:ss").parse(currentTimeString);
                Calendar calendarCurrent = Calendar.getInstance();
                calendarCurrent.setTime(current);
                calendarCurrent.add(Calendar.DATE, 1);

                Date x = calendarCurrent.getTime();
                if (x.after(calendarFrom.getTime()) && x.before(calendarTo.getTime())) {

                    return true;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return false;
    }
}
