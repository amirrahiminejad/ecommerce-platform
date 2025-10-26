package com.webrayan.store.service;

import com.webrayan.store.modules.ads.dto.AdRequestDto;
import com.webrayan.store.modules.ads.service.AdServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AdServiceTest {



    @Autowired
    private AdServiceImpl adService;

 //   @Test
    public void test() {
        AdRequestDto requestDto = new AdRequestDto();
        requestDto.setTitle("Title");
        requestDto.setDescription("Description");
        requestDto.setCategoryId(1l);
        adService.createAd(requestDto);
    }
}
