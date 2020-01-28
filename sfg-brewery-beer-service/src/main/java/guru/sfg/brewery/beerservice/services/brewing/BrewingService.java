package guru.sfg.brewery.beerservice.services.brewing;


import guru.sfg.brewery.beerservice.config.JmsConfig;
import guru.sfg.brewery.beerservice.domain.Beer;
import guru.sfg.brewery.beerservice.repositories.BeerRepository;
import guru.sfg.brewery.beerservice.services.inventory.BeerInventoryService;
import guru.sfg.brewery.beerservice.web.mappers.BeerMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrewingService {
   private final BeerRepository beerRepository;
    private final BeerInventoryService beerInventoryService;
   // private final JmsTemplate jmsTemplate;
    private final BeerMapper beerMapper;
    private final RabbitTemplate rabbitTemplate;

    //public BrewingService(RabbitTemplate rabbitTemplate) {
     //   this.rabbitTemplate = rabbitTemplate;
   // }


    @Scheduled(fixedRate = 5000)//every 5 seconds
    public void checkForLowInventory() {
        List<Beer> beers = beerRepository.findAll();


         beers.forEach(beer -> {


             Integer invQOH = beerInventoryService.getOnhandInventory(beer.getId());

            log.debug("Min Onhand is: " + beer.getMinOnHand());
            log.debug("Inventory is: "  + invQOH);

            if(beer.getMinOnHand() >= invQOH){
                rabbitTemplate.convertAndSend(JmsConfig.BREWING_REQUEST_QUEUE, beer);

    }

});

}

}
