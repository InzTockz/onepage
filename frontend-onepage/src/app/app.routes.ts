import { Routes } from '@angular/router';
import { PedidoDiarioComponent } from './components/pedido-diario/pedido-diario.component';
import { LoginComponent } from './components/login/login.component';
import { NavbarComponent } from './components/navbar/navbar.component';
import { HomeComponent } from './components/home/home.component';
import { LotePedidosComponent } from './components/lote-pedidos/lote-pedidos.component';
import { HistorialEnviosComponent } from './components/historial-envios/historial-envios.component';
import { FacturasCanceladasComponent } from './components/facturas-canceladas/facturas-canceladas.component';
import { FacturasVigentesComponent } from './components/facturas-vigentes/facturas-vigentes.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  {
    path: '',
    component: NavbarComponent,
    children: [
      { path: '', component: HomeComponent },
      { path: 'pedido-diario', component: PedidoDiarioComponent },
      { path: 'lote-pedido', component: LotePedidosComponent },
      { path: 'historial-envios', component: HistorialEnviosComponent },
      { path: 'facturas-canceladas', component: FacturasCanceladasComponent },
      { path: 'facturas-vigentes', component: FacturasVigentesComponent }
    ],
  },
];
