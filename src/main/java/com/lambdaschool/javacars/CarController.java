package com.lambdaschool.javacars;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/cars")
public class CarController
{
    private final CarRepository carrepo;
    private final RabbitTemplate rt;

    public CarController(CarRepository carrepo, RabbitTemplate rt)
    {
        this.carrepo = carrepo;
        this.rt = rt;
    }

    @GetMapping("/id/{id}")
    public Car getById(@PathVariable Long id)
    {
        return carrepo.findById(id).orElseThrow();
    }

    @GetMapping("/year/{year}")
    public List<Car> getByYear(@PathVariable int year)
    {
        return carrepo.findAll().stream().filter(c -> c.getYear() == year)
                .collect(Collectors.toList());
    }

    @GetMapping("/brand/{brand}")
    public List<Car> getByBrand(@PathVariable String brand)
    {
        CarLog message = new CarLog("Searched by brand: " + brand);
        rt.convertAndSend(JavacarsApplication.QUEUE_NAME, message.toString());
        log.info("Message sent - brands searched");
        return carrepo.findAll().stream().filter(c -> c.getBrand().equalsIgnoreCase(brand))
                .collect(Collectors.toList());
    }

    @PostMapping("/upload")
    public List<Car> uploadCars(@RequestBody List<Car> newCars)
    {
        CarLog message = new CarLog("Data loaded");
        rt.convertAndSend(JavacarsApplication.QUEUE_NAME, message.toString());
        log.info("Data loaded");
        return carrepo.saveAll(newCars);
    }

    @DeleteMapping("/delete/{id}")
    public Car deleteCar(@PathVariable Long id)
    {
        Car car = carrepo.findById(id).orElseThrow();
        carrepo.delete(car);
        CarLog message = new CarLog("{" + id + "} Data deleted");
        rt.convertAndSend(JavacarsApplication.QUEUE_NAME, message.toString());
        log.info("{" + id + "} Data deleted");
        return car;
    }
}
