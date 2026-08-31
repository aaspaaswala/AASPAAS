import { Router } from 'express';
import * as reservationController from './reservation.controller';
import { authenticate } from '../../middleware/authenticate';

const router = Router();

router.use(authenticate);

router.post('/', reservationController.createReservation);
router.get('/', reservationController.listReservations);
router.get('/:id', reservationController.getReservation);
router.post('/:id/cancel', reservationController.cancelReservation);

export default router;
