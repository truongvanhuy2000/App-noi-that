package com.huy.appnoithat.Module;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.huy.appnoithat.Controller.GlobalSettingController;
import com.huy.appnoithat.Controller.UserDetailController;
import com.huy.appnoithat.Scene.GlobalSettingScene;
import com.huy.appnoithat.Service.PersistenceStorage.PersistenceStorageService;
import com.huy.appnoithat.Service.PersistenceStorage.StorageService;
import com.huy.appnoithat.Service.RestService.AccountRestService;
import com.huy.appnoithat.Service.RestService.LapBaoGiaRestService;
import com.huy.appnoithat.Service.RestService.PricingModelRestService;
import com.huy.appnoithat.Service.UserDetail.UserDetailService;


public class AppNoiThatModule extends AbstractModule {
    @Override
    protected void configure() {
        super.configure();
        install(new ControllerModule());
        install(new ServiceModule());
        install(new SceneModule());
        install(new RestModule());
        install(new HttpClientModule());
        install(new SessionModule());
    }

    @Provides
    ObjectMapper objectMapper() {
        return JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .serializationInclusion(JsonInclude.Include.NON_NULL)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .build();
    }
}
