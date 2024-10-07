package com.example.api.service;

import com.example.api.domain.Coupon;
import com.example.api.producer.CouponCreateProducer;
import com.example.api.repository.CouponCountRepository;
import com.example.api.repository.CouponRepository;
import org.springframework.stereotype.Service;

@Service
public class ApplyService {

    private final CouponRepository couponRepository;
    private final CouponCountRepository couponCountRepository;
    private final CouponCreateProducer couponCreateProducer;

    public ApplyService(CouponRepository couponRepository,
        CouponCountRepository couponCountRepository,
        CouponCreateProducer couponCreateProducer) {
        this.couponRepository = couponRepository;
        this.couponCountRepository = couponCountRepository;
        this.couponCreateProducer = couponCreateProducer;
    }


    // RaceCondition


    public void apply(Long userId) {
        // Reids는 싱글 스레드기반으로 작동하기 때문에 이전 작업이 끝날때 까지 진행 x
        // 항상 최신 값으로 처리하게 됨 -> 하지만 이 구조에서도 발생하는 문제가 있다.
        // 발생하는 문제
        // 발급하는 쿠폰이 많아지면 많아진다면 DB에 부하가 발생
        // 쿠폰 전용 DB가 아니라면 에러가 전파됨
        long count = couponCountRepository.increment();

        if (count > 100) {
            return;
        }

        couponCreateProducer.create(userId);
    }

}
