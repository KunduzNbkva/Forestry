package kg.forestry.di

import android.content.Context
import androidx.room.Room
import kg.forestry.localstorage.Scheduler
import kg.forestry.repos.AccountRepository
import kg.forestry.ui.auth.AuthViewModel
import kg.forestry.localstorage.Preferences
import kg.forestry.localstorage.db.AppDatabase
import kg.forestry.localstorage.db.DatabaseHelper
import kg.forestry.repos.HarvestRepository
import kg.forestry.repos.PlantsRepository
import kg.forestry.repos.RecordsRepository
import kg.forestry.ui.MapAllPointViewModel
import kg.forestry.ui.biomass.BiomassViewModel
import kg.forestry.ui.cattle_pasture.CattlePastureViewModel
import kg.forestry.ui.cattle_type.CattleTypeViewNodel
import kg.forestry.ui.choose_distance.ChooseDistanceViewModel
import kg.forestry.ui.choose_side.ChooseSideViewModel
import kg.forestry.ui.harvest.HarvestListViewModel
import kg.forestry.ui.harvest.harvest_info.AddHarvestViewModel
import kg.forestry.ui.main.MainViewModel
import kg.forestry.ui.pastures.PastureListViewModel
import kg.forestry.ui.plant.PlantListViewModel
import kg.forestry.ui.plant.plant_info.AddPlantViewModel
import kg.forestry.ui.plant_type.PlatTypeViewModel
import kg.forestry.ui.plots.PlotListViewModel
import kg.forestry.ui.records.NewRecordViewModel
import kg.forestry.ui.reports.ReportViewModel
import kg.forestry.ui.soil_color.SoilColorViewModel
import kg.forestry.ui.soil_erossion.SoilErossionViewModel
import kg.forestry.ui.soil_districts.DistrictListViewModel
import kg.forestry.ui.soil_regions.RegionListViewModel
import kg.forestry.ui.soil_texture.SoilTextureViewModel
import kg.forestry.ui.soil_villages.VillageListViewModel
import kg.forestry.ui.start.StartViewModel
import kg.forestry.ui.tree_type.TreeTypeViewModel
import kg.forestry.ui.user_profile.UserProfileViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val vmModule = module {
    viewModel { MainViewModel(get(),get(),get(),get(),get()) }
    viewModel { AuthViewModel(get(),get()) }
    viewModel { UserProfileViewModel(get()) }
    viewModel { HarvestListViewModel(get()) }
    viewModel { AddHarvestViewModel(get(),get()) }
    viewModel { PlotListViewModel(get()) }
    viewModel { VillageListViewModel(get()) }
    viewModel { RegionListViewModel(get()) }
    viewModel { DistrictListViewModel(get()) }

    viewModel { PastureListViewModel(get()) }
    viewModel { BiomassViewModel() }
    viewModel { NewRecordViewModel(get()) }
    viewModel { PlantListViewModel(get()) }
    viewModel { AddPlantViewModel(get(),get()) }
    viewModel { CattlePastureViewModel() }
    viewModel { CattleTypeViewNodel() }
    viewModel { SoilColorViewModel() }
    viewModel { SoilTextureViewModel() }
    viewModel { StartViewModel() }
    viewModel { ReportViewModel(get()) }
    viewModel { PlatTypeViewModel(get()) }
    viewModel { TreeTypeViewModel(get()) }
    viewModel { ChooseSideViewModel(get()) }
    viewModel { ChooseDistanceViewModel() }
    viewModel { SoilErossionViewModel() }
    viewModel { MapAllPointViewModel(get(),get()) }

}

val appModule = module {
    single { provideDatabase(get()) }
    single { androidContext().resources }
    single { Preferences(get()) }
    single { DatabaseHelper(get()) }
}

val repositoryModule = module {
    factory { HarvestRepository(get(),get(),get()) }
    factory { PlantsRepository(get(),get(),get()) }
    factory { AccountRepository(get(),get(),get()) }
    factory { RecordsRepository(get(),get()) }
}

val networkModule = module {
    single { Scheduler() }
}

private fun provideDatabase(context: Context): AppDatabase {
    return Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.DB_NAME)
        .fallbackToDestructiveMigration()
        .allowMainThreadQueries()
        .build()
}
