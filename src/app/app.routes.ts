import { Routes } from '@angular/router';

export const routes: Routes = [
    {
        path:'',
        loadComponent: ()=>
            import('./features/pages/home/home.component').then(m=>m.HomeComponent),
        pathMatch:'full'
    
    },
    {
        path:'history',
        loadComponent:()=>
            import('./features/pages/history/history.component').then(m=>m.HistoryComponent)
        
    }
];
