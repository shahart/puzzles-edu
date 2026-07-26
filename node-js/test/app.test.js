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

test('GET /solve/20_3?count=true returns the number of solutions', async () => {
  const res = await request(app)
    .get('/solve/20_3?count=true')
    .expect('Content-Type', /json/)
    .expect(200);

  expect(res.body).toBe(2);
}, 60000);

test('GET /solve3d/3_4_5 returns 200 with result 1', async () => {
  const res = await request(app)
    .get('/solve3d/3_4_5')
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

test('handles solve requests concurrently in separate workers', async () => {
  const problemIds = ['12_5', '10_6', '15_4', '20_3'];

  const responses = await Promise.all(
    problemIds.map((problemId) => request(app).get(`/solve/${problemId}`).expect(200))
  );

  expect(responses.map((response) => response.body)).toEqual([1, 1, 1, 1]);
}, 60000);
