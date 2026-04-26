// AvatarService.java
package com.neuroarena.service;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Random;

@Service
public class AvatarService {

    // Your avatar icon URLs - Replace with your actual links
    private static final List<String> AVATAR_ICONS = List.of(
        "https://i.ibb.co/JwLFTLwp/face1.jpg",
        "https://i.ibb.co/chDgnq1V/face2.jpg",
        "https://i.ibb.co/mVgfNj8q/face3.jpg",
        "https://i.ibb.co/N2WGMSbj/face4.jpg",
        "https://i.ibb.co/5hKF68dg/face5.jpg",
        "https://i.ibb.co/Z18fwKXr/face6.jpg",
        "https://i.ibb.co/V0wy62fg/face7.jpg",
        "https://i.ibb.co/tMxZDcvy/face8.jpg",
        "https://i.ibb.co/FbP8kjjP/face9.jpg",
        "https://i.ibb.co/rKvc6WCD/face10.jpg",
        "https://i.ibb.co/QvndCBTV/face11.jpg",
        "https://i.ibb.co/69FhqNL/face12.jpg",
        "https://i.ibb.co/hRQ0Ygmp/face13.jpg",
        "https://i.ibb.co/jYC6Cyh/face14.jpg",
        "https://i.ibb.co/RdLzDwM/face15.jpg",
        "https://i.ibb.co/cfsP29n/face16.jpg",
        "https://i.ibb.co/F4S0NChd/face17.jpg",
        "https://i.ibb.co/Fq0HV3Bm/face18.jpg"
    );

    private final Random random = new Random();

    /**
     * Returns a random avatar icon URL
     */
    public String getRandomAvatarIcon() {
        int index = random.nextInt(AVATAR_ICONS.size());
        return AVATAR_ICONS.get(index);
    }
}