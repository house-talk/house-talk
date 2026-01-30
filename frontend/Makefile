.PHONY: up down logs

up:
	cd .. && docker compose -f docker-compose.local.yml up -d
	npm run dev

down:
	cd .. && docker compose -f docker-compose.local.yml down

logs:
	cd .. && docker compose -f docker-compose.local.yml logs -f
