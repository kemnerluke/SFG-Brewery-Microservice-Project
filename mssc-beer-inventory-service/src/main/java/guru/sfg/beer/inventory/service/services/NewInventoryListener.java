package guru.sfg.beer.inventory.service.services;

import guru.sfg.beer.inventory.service.config.JmsConfig;
import guru.sfg.beer.inventory.service.domain.BeerInventory;
import guru.sfg.beer.inventory.service.repositories.BeerInventoryRepository;
import guru.sfg.brewery.beerservice.domain.Beer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;


@Slf4j
@RequiredArgsConstructor
@Service
public class NewInventoryListener {

    private  final  RabbitTemplate rabbitTemplate;
    //
    private final BeerInventoryRepository beerInventoryRepository;



    @RabbitListener(queues = JmsConfig.NEW_INVENTORY_QUEUE)
    public void listen(Beer event){
        System.out.println("listening for inventoryyyy"+event.toString());


        beerInventoryRepository.save(BeerInventory.builder()
                .beerId(event.getId())
                .upc(event.getUpc())
                .quantityOnHand(event.getQuantityOnHand())
                .build());
    }

}

