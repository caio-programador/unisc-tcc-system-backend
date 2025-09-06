package br.com.unisc.unisctccsystembackend.entities.DTO;

import br.com.unisc.unisctccsystembackend.entities.DeliveryType;
import org.springframework.web.multipart.MultipartFile;

public record DeliveryCreateDTO(
        MultipartFile file,
        Long tccId,
        String tccTitle,
        DeliveryType deliveryType
) {
}
