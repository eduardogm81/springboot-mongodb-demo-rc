package es.quark.springbootmongodbdemo.controller;

import com.querydsl.core.types.dsl.BooleanExpression;
import es.quark.springbootmongodbdemo.domain.Hotel;
import es.quark.springbootmongodbdemo.domain.QHotel;
import es.quark.springbootmongodbdemo.repository.HotelRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hotels")
public class HotelController {


    private final HotelRepository hotelRepository;

    public HotelController(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    @GetMapping("/all")
    public List<Hotel> getAll() {
        List<Hotel> hotels = this.hotelRepository.findAll();
        return hotels;
    }

    @PostMapping
    public void insert(@RequestBody Hotel hotel) {
        this.hotelRepository.insert(hotel);
    }

    @PutMapping
    public void update(@RequestBody Hotel hotel) {
        // hotel hava an id => insert else update
        this.hotelRepository.save(hotel);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id) {
        this.hotelRepository.delete(id);
    }

    @GetMapping("/{id}")
    public Hotel getById(@PathVariable("id") String id) {
        return this.hotelRepository.findById(id);
    }

    @GetMapping("/price/{maxPrice}")
    public List<Hotel> getByPricePerNight(@PathVariable("maxPrice") int maxPrice) {
        return this.hotelRepository.findByPricePerNightLessThan(maxPrice);
    }

    @GetMapping("/city/{city}")
    public List<Hotel> getByCity(@PathVariable("city") String city) {
        return this.hotelRepository.findByCity(city);
    }

    @GetMapping("/country/{country}")
    public List<Hotel> getByCountry(@PathVariable("country") String country) {
        // Create a query class
        QHotel qHotel = new QHotel("hotel");
        // using the query class we can create the filters
        BooleanExpression filterByCountry = qHotel.address.country.eq(country);

        // we can pass the filters to the findall() method
        List<Hotel> hotels = (List<Hotel>) this.hotelRepository.findAll(filterByCountry);
        return hotels;
    }


    @GetMapping("/recommended")
    public List<Hotel> getRecommended() {
        final int maxPrice = 100;
        final int minRating = 7;
        QHotel qHotel = new QHotel("hotel");
        final BooleanExpression filterByPrice = qHotel.pricePerNight.lt(maxPrice);
        final BooleanExpression filterByRating = qHotel.reviews.any().rating.gt(minRating);
        return  (List<Hotel>) this.hotelRepository.findAll(filterByPrice.and(filterByRating));
    }
}
