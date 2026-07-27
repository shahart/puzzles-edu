import { Controller, Get, Param, Res, InternalServerErrorException } from '@nestjs/common';
import { Response } from 'express';
import { Puzzle2D } from '../core/puzzle2d';
import { Puzzle3D } from '../core/puzzle3d';
import { Piece } from '../core/piece';

@Controller()
export class SolveController {

  @Get('solve/:problemId')
  solve(@Param('problemId') problemId: string, @Res({ passthrough: true }) res: Response): void {
    console.log(`Starting id ${problemId}`);

    const rowsCols = problemId.split('_');
    const puzzle2D = new Puzzle2D();
    Piece.totalFill = 0;
    puzzle2D.set(parseInt(rowsCols[0], 10), parseInt(rowsCols[1], 10));

    const abortController = new AbortController();
    const timeout = setTimeout(() => abortController.abort(), 5000);

    try {
      const result = puzzle2D.solve(abortController.signal);
      console.log(`Done id ${problemId} with result ${result}`);
      res.json(result);
    } catch (err) {
      console.error('Solve error:', err);
      throw new InternalServerErrorException((err as Error).message);
    } finally {
      clearTimeout(timeout);
    }
  }

  @Get('solve3d/:problemId')
  solve3d(@Param('problemId') problemId: string, @Res({ passthrough: true }) res: Response): void {
    console.log(`Starting 3D id ${problemId}`);

    try {
      const dimensions = problemId.split('_').map((value) => parseInt(value, 10));
      const puzzle3D = new Puzzle3D(dimensions[0], dimensions[1], dimensions[2]);
      const result = puzzle3D.solve();
      console.log(`Done 3D id ${problemId} with result ${result}`);
      res.json(result);
    } catch (err) {
      console.error('3D solve error:', err);
      throw new InternalServerErrorException((err as Error).message);
    }
  }
}
