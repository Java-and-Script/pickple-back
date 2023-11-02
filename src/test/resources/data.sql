-- Address 정보 삽입
INSERT INTO address_depth1 (name) VALUES ('서울시');

INSERT INTO address_depth2 (name, address_depth1_id) VALUES ('강남구', 1);
INSERT INTO address_depth2 (name, address_depth1_id) VALUES ('강동구', 1);
INSERT INTO address_depth2 (name, address_depth1_id) VALUES ('강북구', 1);
INSERT INTO address_depth2 (name, address_depth1_id) VALUES ('강서구', 1);
INSERT INTO address_depth2 (name, address_depth1_id) VALUES ('관악구', 1);
INSERT INTO address_depth2 (name, address_depth1_id) VALUES ('광진구', 1);
INSERT INTO address_depth2 (name, address_depth1_id) VALUES ('구로구', 1);
INSERT INTO address_depth2 (name, address_depth1_id) VALUES ('금천구', 1);
INSERT INTO address_depth2 (name, address_depth1_id) VALUES ('노원구', 1);
INSERT INTO address_depth2 (name, address_depth1_id) VALUES ('도봉구', 1);
INSERT INTO address_depth2 (name, address_depth1_id) VALUES ('동대문구', 1);
INSERT INTO address_depth2 (name, address_depth1_id) VALUES ('동작구', 1);
INSERT INTO address_depth2 (name, address_depth1_id) VALUES ('마포구', 1);
INSERT INTO address_depth2 (name, address_depth1_id) VALUES ('서대문구', 1);
INSERT INTO address_depth2 (name, address_depth1_id) VALUES ('서초구', 1);
INSERT INTO address_depth2 (name, address_depth1_id) VALUES ('성동구', 1);
INSERT INTO address_depth2 (name, address_depth1_id) VALUES ('성북구', 1);
INSERT INTO address_depth2 (name, address_depth1_id) VALUES ('송파구', 1);
INSERT INTO address_depth2 (name, address_depth1_id) VALUES ('양천구', 1);
INSERT INTO address_depth2 (name, address_depth1_id) VALUES ('영등포구', 1);
INSERT INTO address_depth2 (name, address_depth1_id) VALUES ('용산구', 1);
INSERT INTO address_depth2 (name, address_depth1_id) VALUES ('은평구', 1);
INSERT INTO address_depth2 (name, address_depth1_id) VALUES ('종로구', 1);
INSERT INTO address_depth2 (name, address_depth1_id) VALUES ('중구', 1);
INSERT INTO address_depth2 (name, address_depth1_id) VALUES ('중랑구', 1);

-- Member 정보 삽입
INSERT INTO member (email, nickname, introduction, profile_image_url, status, manner_score, manner_score_count, oauth_id, oauth_provider, address_depth1_id, address_depth2_id) values ('pickple1@pickple.kr', '백둥1', '안녕하세요 백둥1입니다', 'http://profile1.image', '활동', 0, 0, 1, 'KAKAO', 1, 1);
INSERT INTO member (email, nickname, introduction, profile_image_url, status, manner_score, manner_score_count, oauth_id, oauth_provider, address_depth1_id, address_depth2_id) values ('pickple2@pickple.kr', '백둥2', '안녕하세요 백둥2입니다', 'http://profile2.image', '활동', 0, 0, 2, 'KAKAO', 1, 2);
INSERT INTO member (email, nickname, introduction, profile_image_url, status, manner_score, manner_score_count, oauth_id, oauth_provider, address_depth1_id, address_depth2_id) values ('pickple3@pickple.kr', '백둥3', '안녕하세요 백둥3입니다', 'http://profile3.image', '활동', 0, 0, 3, 'KAKAO', 1, 3);
INSERT INTO member (email, nickname, introduction, profile_image_url, status, manner_score, manner_score_count, oauth_id, oauth_provider, address_depth1_id, address_depth2_id) values ('pickple4@pickple.kr', '백둥4', '안녕하세요 백둥4입니다', 'http://profile4.image', '활동', 0, 0, 4, 'KAKAO', 1, 4);
INSERT INTO member (email, nickname, introduction, profile_image_url, status, manner_score, manner_score_count, oauth_id, oauth_provider, address_depth1_id, address_depth2_id) values ('pickple5@pickple.kr', '백둥5', '안녕하세요 백둥5입니다', 'http://profile5.image', '활동', 0, 0, 5, 'KAKAO', 1, 5);
INSERT INTO member (email, nickname, introduction, profile_image_url, status, manner_score, manner_score_count, oauth_id, oauth_provider, address_depth1_id, address_depth2_id) values ('pickple6@pickple.kr', '백둥6', '안녕하세요 백둥6입니다', 'http://profile6.image', '탈퇴', 0, 0, 6, 'KAKAO', 1, 6);
INSERT INTO member (email, nickname, introduction, profile_image_url, status, manner_score, manner_score_count, oauth_id, oauth_provider, address_depth1_id, address_depth2_id) values ('pickple7@pickple.kr', '백둥7', '안녕하세요 백둥7입니다', 'http://profile7.image', '탈퇴', 0, 0, 7, 'KAKAO', 1, 7);
INSERT INTO member (email, nickname, introduction, profile_image_url, status, manner_score, manner_score_count, oauth_id, oauth_provider, address_depth1_id, address_depth2_id) values ('pickple8@pickple.kr', '백둥8', '안녕하세요 백둥8입니다', 'http://profile8.image', '탈퇴', 0, 0, 8, 'KAKAO', 1, 8);
INSERT INTO member (email, nickname, introduction, profile_image_url, status, manner_score, manner_score_count, oauth_id, oauth_provider, address_depth1_id, address_depth2_id) values ('pickple9@pickple.kr', '백둥9', '안녕하세요 백둥9입니다', 'http://profile9.image', '탈퇴', 0, 0, 9, 'KAKAO', 1, 9);
INSERT INTO member (email, nickname, introduction, profile_image_url, status, manner_score, manner_score_count, oauth_id, oauth_provider, address_depth1_id, address_depth2_id) values ('pickple10@pickple.kr', '백둥10', '안녕하세요 백둥10입니다', 'http://profile10.image', '탈퇴', 0, 0, 10, 'KAKAO', 1, 10);
