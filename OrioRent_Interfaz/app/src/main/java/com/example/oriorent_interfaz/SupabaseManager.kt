package com.example.oriorent_interfaz

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime

object SupabaseManager {
    val client = createSupabaseClient(
        supabaseUrl = "https://yxosnfnaxmxxbnnraykd.supabase.co",
        supabaseKey = "sb_publishable_9MD7KtPFHyGWafr8oze4Wg_oRWPbHid"
    ) {
        install(Postgrest)
        install(Realtime)
        install(Auth)
    }
}