const request = require('supertest');
const app = require('../src/app');
const Piece = require('../src/core/Piece');

beforeEach(() => {
  Piece.totalFill = 0;
});

test('GET /solve/5_12 returns 200 with result 1', async () => {
  const res = await request(app)
    .get('/solve/5_12')
    .expect('Content-Type', /json/)
    .expect(200);

  expect(res.body).toBe(1);
}, 60000);

test('sequential valid->invalid->valid keeps working', async () => {
  let res = await request(app).get('/solve/5_12').expect(200);
  expect(res.body).toBe(1);

  res = await request(app).get('/solve/5_13').expect(200);
  expect(res.body).toBe(0);

  res = await request(app).get('/solve/5_12').expect(200);
  expect(res.body).toBe(1);
}, 120000);
