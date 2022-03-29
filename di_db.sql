CREATE SCHEMA di_db;
use di_db;
CREATE TABLE di_db(
  `num` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NULL,
  `id` VARCHAR(45) NULL,
  `pw` VARCHAR(45) NULL,
  PRIMARY KEY (`num`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC) VISIBLE
);
INSERT INTO di_db (name, id, pw) VALUES ('root', 'root', 'root');
INSERT INTO di_db (name, id, pw) VALUES ('root2', 'root2', 'root2');
INSERT INTO di_db (name, id, pw) VALUES ('root3', 'root3', 'root3');


CREATE TABLE keyword(
 `keyword` VARCHAR(50) NOT NULL,
  PRIMARY KEY (`keyword`)
);

INSERT INTO keyword values("사과");
INSERT INTO keyword values("바나나");
INSERT INTO keyword values("딸기");
INSERT INTO keyword values("수박");
INSERT INTO keyword values("자동차");
INSERT INTO keyword values("오토바이");
INSERT INTO keyword values("버스");
INSERT INTO keyword values("택시");
INSERT INTO keyword values("강아지");
INSERT INTO keyword values("토끼");
INSERT INTO keyword values("기린");
INSERT INTO keyword values("코끼리");
INSERT INTO keyword values("라면");
INSERT INTO keyword values("치킨");
INSERT INTO keyword values("커피");
INSERT INTO keyword values("퇴근");
INSERT INTO keyword values("출근");
INSERT INTO keyword values("아이폰");
INSERT INTO keyword values("카카오톡");
INSERT INTO keyword values("유튜브");
INSERT INTO keyword values("노트북");
INSERT INTO keyword values("태풍");
INSERT INTO keyword values("해리포터");
INSERT INTO keyword values("반지의제왕");
INSERT INTO keyword values("타이타닉");
INSERT INTO keyword values("겨울왕국");
INSERT INTO keyword values("백두산");
INSERT INTO keyword values("첫눈");
INSERT INTO keyword values("볼펜");
INSERT INTO keyword values("패딩");
INSERT INTO keyword values("셔츠");
INSERT INTO keyword values("마스크");
INSERT INTO keyword values("배달의민족");
INSERT INTO keyword values("검정색");
INSERT INTO keyword values("성탄절");
INSERT INTO keyword values("바이올린");
INSERT INTO keyword values("피아노");
INSERT INTO keyword values("기타");
INSERT INTO keyword values("탈출구");
INSERT INTO keyword values("물건");
INSERT INTO keyword values("유리구두");
INSERT INTO keyword values("오페라");
INSERT INTO keyword values("노래방");
INSERT INTO keyword values("리본");
INSERT INTO keyword values("머리띠");
INSERT INTO keyword values("감자탕");
INSERT INTO keyword values("야구");
INSERT INTO keyword values("축구");
INSERT INTO keyword values("미식축구");
INSERT INTO keyword values("로켓");
commit;