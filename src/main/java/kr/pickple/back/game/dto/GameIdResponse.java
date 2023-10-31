package kr.pickple.back.game.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(staticName = "from")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameIdResponse {

    private Long hostId;
}
