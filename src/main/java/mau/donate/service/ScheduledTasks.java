package mau.donate.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    public ScheduledTasks() {
    }

    @Scheduled(fixedRate = 1000 * 60 * 5, initialDelay = 1000 * 60 * 5)
    public void each5m() {
    }

    @Scheduled(fixedRate = 1000 * 60 * 60 * 3, initialDelay = 1000 * 60 * 60)
    public void each3h() {
    }

    @Scheduled(fixedRate = 1000 * 60 * 60 * 24 * 7, initialDelay = 1000 * 60 * 60)
    public void each7d() {
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void midNight() {
        System.gc();
    }


}