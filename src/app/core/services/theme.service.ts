import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ThemeService {

  private darkMode = true;

  constructor() {

    const savedTheme =
      localStorage.getItem('theme');

    this.darkMode =
      savedTheme !== 'light';

    this.applyTheme();

  }

  toggleTheme(): void {

    this.darkMode =
      !this.darkMode;

    localStorage.setItem(
      'theme',
      this.darkMode ? 'dark' : 'light'
    );

    this.applyTheme();

  }

  applyTheme(): void {

    const html =
      document.documentElement;

    if (this.darkMode) {

      html.classList.add('dark');

    } else {

      html.classList.remove('dark');

    }

  }

  isDarkMode(): boolean {

    return this.darkMode;

  }

}