package net.octopvp.octocore.master.component;

import lombok.Getter;
import net.badbird5907.lightning.EventBus;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@Getter
public class LightningHolder {
    private EventBus eventBus =  new EventBus();

    @Bean
    public EventBus lightning() {
        return eventBus;
    }
}
