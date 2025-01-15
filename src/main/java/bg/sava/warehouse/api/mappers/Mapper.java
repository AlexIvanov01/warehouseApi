package bg.sava.warehouse.api.mappers;

import bg.sava.warehouse.api.models.Product;
import bg.sava.warehouse.api.models.dtos.ProductCreateDto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class Mapper {

    private final ModelMapper modelMapper;

    public Mapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public ProductCreateDto mapTo(Product product) {
        return modelMapper.map(product, ProductCreateDto.class);
    }

    public Product mapFrom(ProductCreateDto dto) {
        return modelMapper.map(dto, Product.class);
    }
}
