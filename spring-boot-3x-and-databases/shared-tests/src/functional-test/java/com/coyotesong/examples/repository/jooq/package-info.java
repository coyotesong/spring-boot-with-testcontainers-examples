/*
 * Copyright (c) 2024 Bear Giles <bgiles@coyotesong.com>.
 * All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Repository tests using jOOQ
 *
 * These classes only exist to:
 *
 * - specify the persistence mechanism (via the `@ActiveProfiles` annotation)
 * - provide the database test container
 *
 * The database test container is defined here in order to eliminate problems
 * due to race conditions when multiple test classes share a common resource.
 * This can be fixed - later.
 */
@SuppressWarnings({"JavadocBlankLines"})
package com.coyotesong.examples.repository.jooq;