import { Component } from '@angular/core';
import { RouterOutlet, RouterLinkWithHref, RouterLinkActive } from '@angular/router';
import {
  LucideFileText,
  LucideHome,
  LucideUser,
  LucideChevronDown,
  LucideBell,
  LucideSearch,
  LucideLogOut,
} from '@lucide/angular';

@Component({
  selector: 'app-navbar',
  imports: [
    RouterOutlet,
    RouterLinkWithHref,
    RouterLinkActive,
    LucideFileText,
    LucideHome,
    LucideUser,
    LucideChevronDown,
    LucideBell,
    LucideSearch,
    LucideLogOut,
  ],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css',
})
export class NavbarComponent {
  primeraGestion: boolean = false;
  segundaGestion: boolean = false;
  terceraGestion: boolean = false;
}
