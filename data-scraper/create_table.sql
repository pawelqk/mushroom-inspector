CREATE TABLE IF NOT EXISTS mushrooms_search_result(
   id             VARCHAR(14) NOT NULL PRIMARY KEY
  ,title          VARCHAR(14) NOT NULL
  ,pageid         INTEGER  NOT NULL
  ,repository     VARCHAR(8) NOT NULL
  ,url            VARCHAR(40) NOT NULL
  ,concepturi     VARCHAR(60) NOT NULL
  ,label          VARCHAR(150) NOT NULL
  ,description    VARCHAR(300) NOT NULL
  ,aliases0       VARCHAR(20)
);
