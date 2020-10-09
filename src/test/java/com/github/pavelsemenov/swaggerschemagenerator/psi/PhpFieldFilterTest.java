package com.github.pavelsemenov.swaggerschemagenerator.psi;

import com.jetbrains.php.lang.psi.resolve.types.PhpType;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class PhpFieldFilterTest {
    PhpFieldFilter fieldFilter = new PhpFieldFilter();

    @Test
    public void testBannedTypes() {
        PhpFieldFilter.BANNED_TYPES.forEach(t -> {
            assertThat(fieldFilter.isBanned(t)).isTrue();
            assertThat(fieldFilter.isBanned(PhpType.builder().add(t).build())).isTrue();
        });
    }

    @Test
    public void testNotBannedTypes() {
        Arrays.asList(PhpType._INT, PhpType._STRING, PhpType._BOOL).forEach(t -> {
            assertThat(fieldFilter.isBanned(t)).isFalse();
            assertThat(fieldFilter.isBanned(PhpType.builder().add(t).build())).isFalse();
        });
    }

    @Test
    public void testFirstTypeSingle() {
        PhpType type = PhpType.builder().add(PhpType._STRING).build();
        assertThat(fieldFilter.getFirstType(type)).isEqualTo(PhpType._STRING);
    }

    @Test
    public void testFirstTypeNullable() {
        PhpType type = PhpType.builder().add(PhpType._INT).add(PhpType._NULL).build();
        assertThat(fieldFilter.getFirstType(type)).isEqualTo(PhpType._INT);
    }

    @Test
    public void testFirstTypeMultiple() {
        PhpType type = PhpType.builder().add(PhpType._INT).add(PhpType._STRING).build();
        assertThat(fieldFilter.getFirstType(type)).isEqualTo(PhpType._NULL);
    }

    @Test
    public void testFirstTypeMultipleNullable() {
        PhpType type = PhpType.builder().add(PhpType._INT).add(PhpType._STRING).add(PhpType._NULL).build();
        assertThat(fieldFilter.getFirstType(type)).isEqualTo(PhpType._NULL);
    }
}