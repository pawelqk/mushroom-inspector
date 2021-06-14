search:
	docker run -it --rm --name my-running-script -v "${PWD}":/usr/src/myapp -w /usr/src/myapp php:7.4-cli-alpine php scrape_search.php

convert_search:
	docker run -it --rm --name my-running-script -v "${PWD}":/usr/src/myapp -w /usr/src/myapp php:7.4-cli-alpine php convert_to_db_search.php

items:
	docker run -it --rm --name my-running-script -v "${PWD}":/usr/src/myapp -w /usr/src/myapp php:7.4-cli-alpine php db_to_items.php

convert_data:
	docker run -it --rm --name my-running-script -v "${PWD}":/usr/src/myapp -w /usr/src/myapp php:7.4-cli-alpine php convert_to_db_shroomdata.php
