import { Module } from '@nestjs/common';
import { SolveController } from './solve.controller';

@Module({
  controllers: [SolveController],
})
export class SolveModule {}
