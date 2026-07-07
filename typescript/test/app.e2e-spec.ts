import { Test, TestingModule } from '@nestjs/testing';
import { INestApplication } from '@nestjs/common';
import request from 'supertest';
import { AppModule } from '../src/app.module';
import { Piece } from '../src/core/piece';

describe('SolveController (e2e)', () => {
  let app: INestApplication;

  beforeEach(async () => {
    const moduleFixture: TestingModule = await Test.createTestingModule({
      imports: [AppModule],
    }).compile();

    app = moduleFixture.createNestApplication();
    await app.init();
    Piece.totalFill = 0;
  });

  afterEach(async () => {
    await app.close();
  });

  it('GET /solve/5_12 returns 200 with result 1', async () => {
    const res = await request(app.getHttpServer())
      .get('/solve/5_12')
      .expect(200);

    expect(res.body).toBe(1);
  }, 60000);

  it('sequential valid->invalid->valid keeps working', async () => {
    let res = await request(app.getHttpServer()).get('/solve/5_12').expect(200);
    expect(res.body).toBe(1);

    res = await request(app.getHttpServer()).get('/solve/5_13').expect(200);
    expect(res.body).toBe(0);

    res = await request(app.getHttpServer()).get('/solve/5_12').expect(200);
    expect(res.body).toBe(1);
  }, 120000);
});
