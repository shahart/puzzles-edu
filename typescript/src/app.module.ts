import { Module } from '@nestjs/common';
import { SolveModule } from './solve/solve.module';

@Module({
  imports: [SolveModule],
})
export class AppModule {}
