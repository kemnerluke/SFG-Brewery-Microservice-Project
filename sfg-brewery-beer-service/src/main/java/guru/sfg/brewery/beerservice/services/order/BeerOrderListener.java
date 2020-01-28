package guru.sfg.brewery.beerservice.services.order;


import guru.sfg.beer.order.service.domain.BeerOrder;
import guru.sfg.brewery.beerservice.config.JmsConfig;
import guru.sfg.brewery.beerservice.domain.Beer;
import guru.sfg.brewery.beerservice.model.BeerStyleEnum;
import guru.sfg.brewery.beerservice.repositories.BeerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;


@Slf4j
@RequiredArgsConstructor
@Service
public class BeerOrderListener {

   // private final JmsTemplate jmsTemplate;
    private final BeerRepository beerRepository;
    private final RabbitTemplate rabbitTemplate;

    public static final String NEW_BEER_ORDER_QUEUE = "new-beer-order";



    @Transactional
    @JmsListener(destination = JmsConfig.NEW_BEER_ORDER_QUEUE)
    @Scheduled(fixedRate = 5000)//every 5 seconds
    public void listen(){

        BeerOrder beerOrder= (BeerOrder) rabbitTemplate.receiveAndConvert(JmsConfig.NEW_BEER_ORDER_QUEUE);
 log.debug("im here in beer order inventorty"+beerOrder.toString());

        if (beerOrder != null) {

            beerOrder.getBeerOrderLines().forEach(beerOrderLine -> {
                log.debug("Beer Order Line" + beerOrderLine.toString());
                Beer beer = beerRepository.findByUpc(beerOrderLine.getUpc());
                rabbitTemplate.convertAndSend(JmsConfig.BREWING_REQUEST_QUEUE, beer);
                log.debug("Sending Beer Order to Brewing Request");
            });
        }
        else{
            log.warn("beer order - test beerorder is null");
        }

}
}