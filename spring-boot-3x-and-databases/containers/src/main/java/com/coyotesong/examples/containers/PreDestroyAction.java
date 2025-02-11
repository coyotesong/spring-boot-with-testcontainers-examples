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

package com.coyotesong.examples.containers;

import java.util.function.Function;

/**
 * Pre-destruction action
 */
public interface PreDestroyAction<T, R> extends Function<T, R> {
    /**
     * Get name of the action
     *
     * @return name of action
     */
    String getName();

    /**
     * {@inheritDoc}
     */
    @Override
    R apply(T container);
}
