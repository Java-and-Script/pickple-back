package kr.pickple.back.address.service;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kr.pickple.back.address.domain.MainAddress;
import kr.pickple.back.address.dto.response.AllAddressResponse;
import kr.pickple.back.address.implement.AddressReader;

@SpringBootTest
class AddressServiceTest {

    @Autowired
    private AddressService addressService;

    @Autowired
    private AddressReader addressReader;

    @Test
    @DisplayName("지역 목록 조회 시, 전체 도, 시, 구 정보를 반환한다.")
    void findAllAddress_ReturnAllAddressNames() {
        //when
        AllAddressResponse allAddressResponse = addressService.findAllAddress();

        //then
        assertThat(allAddressResponse.getAddressDepth1()).isNotNull();
        assertThat(allAddressResponse.getAddressDepth2List()).isNotEmpty();
    }

    @Test
    @DisplayName("주 활동 지역 조회 시, 올바른 주소1, 주소2 이름을 넣었을 때, 각각의 주소 객체를 반환한다.")
    void findMainAddressByNames_ValidAddressNames_ReturnMainAddressInstance() {
        //given
        String addressDepth1Name = "서울시";
        String addressDepth2Name = "강남구";

        //when
        MainAddress mainAddress = addressReader.readMainAddressByNames(addressDepth1Name, addressDepth2Name);

        //then
        assertThat(mainAddress.getAddressDepth1Name()).isEqualTo(addressDepth1Name);
        assertThat(mainAddress.getAddressDepth2Name()).isEqualTo(addressDepth2Name);
    }
}
